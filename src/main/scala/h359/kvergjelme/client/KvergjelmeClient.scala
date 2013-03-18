package h359.kvergjelme.client

import swing.{SimpleSwingApplication}
import javax.swing.{UIManager}

object KvergjelmeClient extends SimpleSwingApplication {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
	val top = new KvergjelmeFrame
}