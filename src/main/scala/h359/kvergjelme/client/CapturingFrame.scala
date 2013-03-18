package h359.kvergjelme.client

import swing._
import java.awt.{GraphicsEnvironment, Dimension, Color, Toolkit}
import java.awt.GraphicsDevice.WindowTranslucency._

class CapturingFrame extends Frame {
	val gd = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
	if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
		println("Translucency not supported. Aborting.")
		System.exit(0)
	}
	peer.setAlwaysOnTop(true)
	peer.setUndecorated(true)
	size = Toolkit.getDefaultToolkit.getScreenSize // new Dimension(640, 360)
	background = new Color(0, 0, 0, 0.5f)
	visible = true
}