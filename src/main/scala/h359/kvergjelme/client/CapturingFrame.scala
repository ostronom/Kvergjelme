package h359.kvergjelme.client

import swing._
import swing.Swing._
import swing.event._
import java.awt.{Point, Dimension, Color, Cursor}
import javax.swing.{SwingUtilities}

class CapturingFrame extends Frame {
	peer.setAlwaysOnTop(true)
	peer.setUndecorated(true)
	background = new Color(0, 0, 0, 0.5f)
	preferredSize  = new Dimension(640, 360)
	visible = true
	var originX:Int = 0
	var originY:Int = 0
	var isResizing:Boolean = false
	val parentPeer = peer

	// we need panel here to gain access to mouse events
	val virtualPanel = new Panel {
		preferredSize  = new Dimension(640, 360)
		background = new Color(0, 0, 0, 0.5f)
		listenTo(mouse.clicks, mouse.moves)
		reactions += {
			case e: MouseMoved =>
				val brCorner = new Point { x = size.getWidth.intValue; y = size.getHeight.intValue }
				if (e.point.distance(brCorner) <= 10) {
					cursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)
					isResizing = true
				}
				else
				{
					cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
					isResizing = false
				}
			case e: MousePressed =>
				if (!isResizing)
				{
					originX = e.peer.getX
					originY = e.peer.getY
					cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
				}
			case e: MouseDragged =>
				if (isResizing) {
					// flip on negative size is not supported
					val tlCorner = parentPeer.getLocationOnScreen()
					val newX = e.peer.getXOnScreen - tlCorner.x
					val newY = e.peer.getYOnScreen - tlCorner.y
					if (newX > 10 && newY > 10) {
						parentPeer.setSize(new Dimension(newX, newY))
					}
				}
				else {
					parentPeer.setLocation(e.peer.getXOnScreen - originX, e.peer.getYOnScreen - originY)
				}
			case e: MouseReleased =>
				if (isResizing) {
					isResizing = false
				}
				cursor = Cursor.getDefaultCursor()
		}
	}

	contents = virtualPanel
}