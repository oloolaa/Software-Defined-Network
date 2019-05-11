from mininet.topo import Topo

class MyTopo( Topo ):
    "Simple topology example."

    def __init__( self ):
        "Create custom topo."

        # Initialize topology
        Topo.__init__( self )

        # Add hosts and switches
        H1 = self.addHost( 'h1', ip="10.0.0.2")
        H2 = self.addHost( 'h2', ip="10.0.0.3")
        H3 = self.addHost( 'h3', ip="10.0.0.4")
        H4 = self.addHost( 'h4', ip="10.0.0.5")
        S1 = self.addSwitch( 's1' )

        # Add links
        self.addLink( H1, S1 )
        self.addLink( H2, S1 )
        self.addLink( H3, S1 )
        self.addLink( H4, S1 )

topos = { 'mytopo': ( lambda: MyTopo() ) }
