import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Command {
    public static void main(String[] args) throws IOException, InterruptedException{
        // Generate firewall rule.
        BufferedReader type = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            PrintWriter writer = new PrintWriter("/home/floodlight/firewall.sh");

            // Start syntax.
            writer.println("#!/bin/bash");
            System.out.println("Input number to select:");
            System.out.println("1. Status of the firewall");
            System.out.println("2. Enable the firewall");
            System.out.println("3. Disable the firewall");
            System.out.println("4. List all existing rules in json format");
            System.out.println("5. Create new firewall rule");
            System.out.println("6. Delete a rule by ruleid");
            System.out.println("7. Exit");
            System.out.println();
            System.out.print("Input: ");
            String line = type.readLine();

            // Exit.
            if (line.equals("7")) {
                break;
            }
            // Status.
            else if (line.equals("1")) {
                String cmd = "curl http://localhost:8080/wm/firewall/module/status/json";
                writer.println(cmd);
            }
            // Enable.
            else if (line.equals("2")) {
                String cmd = "curl http://localhost:8080/wm/firewall/module/enable/json -X PUT -d ''";
                writer.println(cmd);
            }
            // Disable.
            else if (line.equals("3")) {
                String cmd = "curl http://localhost:8080/wm/firewall/module/disable/json -X PUT -d ''";
                writer.println(cmd);
            }
            // List.
            else if (line.equals("4")) {
                String cmd = "curl http://localhost:8080/wm/firewall/rules/json";
                writer.println(cmd);
            }

            // Create.
            else if (line.equals("5")) {
                System.out.print("<1>Switch, <2>Host: ");
                line = type.readLine();

                // Add rules on switch.
                if (line.equals("1")) {
                    System.out.print("Input switchid: ");
                    String switchid = type.readLine();

                    System.out.print("<1>ALLOW, <2>DENY: ");
                    String action = (line = type.readLine()).equals("1") ? "ALLOW" : "DENY";

                    String cmd = "curl -X POST -d '{\"switchid\":\"" + switchid +
                            "\", \"action\":\"" + action +
                            "\"}' http://localhost:8080/wm/firewall/rules/json";
                    writer.println(cmd);
                }
                // Add rules on host.
                else if (line.equals("2")) {
                    String srcMac = "", desMac = "", srcIP = "", desIP = "";
                    String protocol = "", srcPort = "", desPort = "", action = "";

                    System.out.print("<1>MAC, <2>IP: ");
                    line = type.readLine();

                    // Based on MAC address.
                    if (line.equals("1")) {
                        System.out.print("Input source MAC: ");
                        srcMac = type.readLine();
                        System.out.print("Input destination MAC: ");
                        desMac = type.readLine();
                        System.out.print("<1>ALLOW, <2>DENY: ");
                        action = ((line = type.readLine()).equals("1") ? "ALLOW" : "DENY");

                        String cmd = "curl -X POST -d '{\"src-mac\":\"" + srcMac +
                                "\", \"dst-mac\":\"" + desMac +
                                "\", \"action\":\"" + action +
                                "\"}' http://localhost:8080/wm/firewall/rules/json";
                        writer.println(cmd);

                        System.out.print("Add reverse rule? <1>Yes, <2>No: ");
                        if ((line = type.readLine()).equals("1")) {
                            cmd = "curl -X POST -d '{\"src-mac\":\"" + desMac +
                                    "\", \"dst-mac\":\"" + srcMac +
                                    "\", \"action\":\"" + action +
                                    "\"}' http://localhost:8080/wm/firewall/rules/json";
                            writer.println(cmd);
                        }
                    }
                    // Based on IP address.
                    else if (line.equals("2")) {
                        System.out.print("Input source IP: ");
                        srcIP = type.readLine();
                        System.out.print("Input destination IP: ");
                        desIP = type.readLine();

                        System.out.print("<1>ARP, <2>IPv4: ");
                        line =type.readLine();
                        // Based on ARP.
                        if (line.equals("1")) {
                            System.out.print("<1>ALLOW, <2>DENY: ");
                            action = (line = type.readLine()).equals("1") ? "ALLOW" : "DENY";

                            String cmd = "curl -X POST -d '{\"src-ip\":\"" + srcIP +
                                    "\", \"dst-ip\":\"" + desIP +
                                    "\", \"dl-type\":\"ARP\", \"action\":\"" + action +
                                    "\"}' http://localhost:8080/wm/firewall/rules/json";
                            writer.println(cmd);

                            System.out.print("Add reverse rule? <1>Yes, <2>No: ");
                            if ((line = type.readLine()).equals("1")) {
                                cmd = "curl -X POST -d '{\"src-ip\":\"" + desIP +
                                        "\", \"dst-ip\":\"" + srcIP +
                                        "\", \"dl-type\":\"ARP\", \"action\":\"" + action +
                                        "\"}' http://localhost:8080/wm/firewall/rules/json";
                                writer.println(cmd);
                            }
                        }
                        // Based on IPv4.
                        else if (line.equals("2")) {
                            boolean hasProtocol = true, hasPorts = true;

                            System.out.print("<1>TCP, <2>UDP, <3>ICMP, <4>none: ");
                            line = type.readLine();
                            if (line.equals("1")) {
                                protocol = "TCP";
                            }
                            else if (line.equals("2")) {
                                protocol = "UDP";
                            }
                            else if (line.equals("3")) {
                                protocol = "ICMP";
                            }
                            else if (line.equals("4")) {
                                hasProtocol = false;
                            }

                            System.out.print("<1>Set up ports, <2>none: ");
                            line = type.readLine();
                            if (line.equals("1")) {
                                System.out.print("Input source ports: ");
                                srcPort = type.readLine();
                                System.out.print("Input destination ports: ");
                                desPort = type.readLine();
                            }
                            else if (line.equals("2")) {
                                hasPorts = false;
                            }

                            System.out.print("<1>ALLOW, <2>DENY: ");
                            action = (line = type.readLine()).equals("1") ? "ALLOW" : "DENY";

                            String cmd;
                            if (hasProtocol && hasPorts) {
                                cmd = "curl -X POST -d '{\"src-ip\":\"" + srcIP +
                                        "\", \"dst-ip\":\"" + desIP +
                                        "\", \"dl-type\":\"IPv4\", \"nw-proto\":\"" + protocol +
                                        "\", \"tp-src\":\"" + srcPort +
                                        "\", \"tp-dst\":\"" + desPort +
                                        "\", \"action\":\"" + action +
                                        "\"}' http://localhost:8080/wm/firewall/rules/json";
                                writer.println(cmd);

                                System.out.print("Add reverse rule? <1>Yes, <2>No: ");
                                if ((line = type.readLine()).equals("1")) {
                                    cmd = "curl -X POST -d '{\"src-ip\":\"" + desIP +
                                            "\", \"dst-ip\":\"" + srcIP +
                                            "\", \"dl-type\":\"IPv4\", \"nw-proto\":\"" + protocol +
                                            "\", \"tp-src\":\"" + desPort +
                                            "\", \"tp-dst\":\"" + srcPort +
                                            "\", \"action\":\"" + action +
                                            "\"}' http://localhost:8080/wm/firewall/rules/json";
                                    writer.println(cmd);
                                }
                            }
                            else if (hasProtocol && !hasPorts) {
                                cmd = "curl -X POST -d '{\"src-ip\":\"" + srcIP +
                                        "\", \"dst-ip\":\"" + desIP +
                                        "\", \"dl-type\":\"IPv4\", \"nw-proto\":\"" + protocol +
                                        "\", \"action\":\"" + action +
                                        "\"}' http://localhost:8080/wm/firewall/rules/json";
                                writer.println(cmd);

                                System.out.print("Add reverse rule? <1>Yes, <2>No: ");
                                if ((line = type.readLine()).equals("1")) {
                                    cmd = "curl -X POST -d '{\"src-ip\":\"" + desIP +
                                            "\", \"dst-ip\":\"" + srcIP +
                                            "\", \"dl-type\":\"IPv4\", \"nw-proto\":\"" + protocol +
                                            "\", \"action\":\"" + action +
                                            "\"}' http://localhost:8080/wm/firewall/rules/json";
                                    writer.println(cmd);
                                }
                            }
                            else if (!hasProtocol && hasPorts) {
                                cmd = "curl -X POST -d '{\"src-ip\":\"" + srcIP +
                                        "\", \"dst-ip\":\"" + desIP +
                                        "\", \"dl-type\":\"IPv4\", \"tp-src\":\"" + srcPort +
                                        "\", \"tp-dst\":\"" + desPort +
                                        "\", \"action\":\"" + action +
                                        "\"}' http://localhost:8080/wm/firewall/rules/json";
                                writer.println(cmd);

                                System.out.print("Add reverse rule? <1>Yes, <2>No: ");
                                if ((line = type.readLine()).equals("1")) {
                                    cmd = "curl -X POST -d '{\"src-ip\":\"" + desIP +
                                            "\", \"dst-ip\":\"" + srcIP +
                                            "\", \"dl-type\":\"IPv4\", \"tp-src\":\"" + desPort +
                                            "\", \"tp-dst\":\"" + srcPort +
                                            "\", \"action\":\"" + action +
                                            "\"}' http://localhost:8080/wm/firewall/rules/json";
                                    writer.println(cmd);
                                }
                            }
                            else if (!hasProtocol && !hasPorts) {
                                cmd = "curl -X POST -d '{\"src-ip\":\"" + srcIP +
                                        "\", \"dst-ip\":\"" + desIP +
                                        "\", \"action\":\"" + action +
                                        "\"}' http://localhost:8080/wm/firewall/rules/json";
                                writer.println(cmd);

                                System.out.print("Add reverse rule? <1>Yes, <2>No: ");
                                if ((line = type.readLine()).equals("1")) {
                                    cmd = "curl -X POST -d '{\"src-ip\":\"" + desIP +
                                            "\", \"dst-ip\":\"" + srcIP +
                                            "\", \"action\":\"" + action +
                                            "\"}' http://localhost:8080/wm/firewall/rules/json";
                                    writer.println(cmd);
                                }
                            }
                        }
                    }
                }
            }
            // Delete.
            else if (line.equals("6")) {
                System.out.print("Input rule number: ");
                line = type.readLine();
                String cmd = "curl -X DELETE -d '{\"ruleid\":\"" + line +
                        "\"}' http://localhost:8080/wm/firewall/rules/json";
                writer.println(cmd);
            }

            // Finish writing and start the srcipt.
            writer.close();
            Process p;
            List<String> cmdList = new ArrayList<String>();
            cmdList.add("sh");
            cmdList.add("/home/floodlight/firewall.sh");
            p = new ProcessBuilder(cmdList).start();
            p.waitFor();

            // Load the output.
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }

            // End syntax.
            System.out.println();
            System.out.print("Press ENTER to continue.");
            type.readLine();
            System.out.println("========================================");
            System.out.println();
        }
    }
}

