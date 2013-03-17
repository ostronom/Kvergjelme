package h359.kvergjelme.client

import swing.{MainFrame, BoxPanel, Orientation, Button, ToggleButton, Label}

object chooseCapturingRegion extends Button {
	text = "Выбрать регион захвата"
}

object toggleStreaming extends ToggleButton {
	text = "Захват"
}

object statusIndicator extends Label {
	text = "[оффлайн]"
}

class KvergjelmeFrame extends MainFrame {
	title = "Kvergjelme Client"
	contents = new BoxPanel( Orientation.Horizontal ) {
		contents += chooseCapturingRegion
		contents += toggleStreaming
		contents += statusIndicator
	}
}