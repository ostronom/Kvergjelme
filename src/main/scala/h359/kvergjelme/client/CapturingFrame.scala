package h359.kvergjelme.client

import swing._
import swing.Swing._
import swing.event._
import java.awt.{Point, Dimension, Rectangle, Color, Cursor}
import javax.swing.{SwingUtilities}

class CapturingFrame(region: Rectangle) extends Frame with Publisher {
	peer.setAlwaysOnTop(true)
	peer.setUndecorated(true)
	background = new Color(0, 0, 0, 0.5f)
	preferredSize = region.getSize
	location = region.getLocation
	visible = true
	private var originX:Int = 0
	private var originY:Int = 0
	private var isResizing:Boolean = false
	private val me:Frame = this

	// we need panel here to gain access to mouse events
	private val virtualPanel = new Panel {
		focusable = true
		preferredSize = region.getSize
		background = new Color(0, 0, 0, 0.5f)
		listenTo(mouse.clicks, mouse.moves, keys)
		reactions += {
			case KeyPressed(_, Key.Enter,_, _) =>
				val newRegion = new Rectangle(me.location, me.size)
				me.publish(new CaptureRegionChange(newRegion))
				me.close()
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
					val tlCorner = me.peer.getLocationOnScreen()
					val newX = e.peer.getXOnScreen - tlCorner.x
					val newY = e.peer.getYOnScreen - tlCorner.y
					if (newX > 10 && newY > 10) {
						me.peer.setSize(new Dimension(newX, newY))
					}
				}
				else {
					me.peer.setLocation(e.peer.getXOnScreen - originX, e.peer.getYOnScreen - originY)
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