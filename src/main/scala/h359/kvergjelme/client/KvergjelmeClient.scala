package h359.kvergjelme.client

import swing.{SimpleSwingApplication, Swing}
import javax.swing.{UIManager, JFrame}
import java.awt.{GraphicsEnvironment}
import java.awt.GraphicsDevice.WindowTranslucency._

object KvergjelmeClient extends SimpleSwingApplication {
	val top = new KvergjelmeFrame

	override def main(args: Array[String]) = Swing.onEDT {
		val gd = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
		if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
			println("Translucency not supported. Aborting.")
			System.exit(0)
		}
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
		startup(args)
	}
}