package h359.kvergjelme.client

import swing._
import swing.event._

object chooseCapturingRegion extends Button {
	text = "Выбрать регион захвата"
}

object toggleStreaming extends ToggleButton {
	text = "Вещание"
	enabled = false
}

object statusIndicator extends Label {
	text = "[оффлайн]"
}

class KvergjelmeFrame extends MainFrame with Publisher {
	title = "Kvergjelme Client"
	contents = new BoxPanel( Orientation.Horizontal ) {
		contents += chooseCapturingRegion
		contents += toggleStreaming
		contents += statusIndicator
	}
	listenTo(chooseCapturingRegion, toggleStreaming)
	reactions += {
		case ButtonClicked(`chooseCapturingRegion`) =>
			new CapturingFrame
		case ButtonClicked(`toggleStreaming`) =>
			println("toggleStreaming button clicked")
	}
}