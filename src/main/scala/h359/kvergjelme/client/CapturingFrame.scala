package h359.kvergjelme.client

import swing._
import swing.Swing._
import swing.event._
import java.awt.{Point, Dimension, Color, Toolkit}
import javax.swing.{SwingUtilities}

class CapturingFrame extends Frame {
	peer.setAlwaysOnTop(true)
	peer.setUndecorated(true)
	//size = Toolkit.getDefaultToolkit.getScreenSize
	background = new Color(0, 0, 0, 0.5f)
	preferredSize  = new Dimension(640, 360)
	visible = true

	// we need panel here to gain access to mouse events
	val virtualPanel = new Panel {
		preferredSize  = new Dimension(640, 360)
		background = new Color(0, 0, 0, 0.5f)
		listenTo(mouse.clicks, mouse.moves)
		reactions += {
			case e: MousePressed => moveFrame(e.point)
			case e: MouseDragged => moveFrame(e.point)
		}
	}

	def moveFrame(p: Point) {
		SwingUtilities.convertPointToScreen(p, virtualPanel.peer)
		p.translate(-location.getX.intValue, -location.getY.intValue)
		peer.setLocation(p)
	}

	contents = virtualPanel
}