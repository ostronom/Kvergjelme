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

object sinkURLInput extends TextField {
	text = "rtmp://<ADDRESS>:1975"
}

class KvergjelmeFrame extends MainFrame with Publisher {
	private var region = new Rectangle(0, 0, 640, 360)
	private val streamer = new Streamer
	title = "Kvergjelme Client"
	contents = new BoxPanel( Orientation.Vertical ) {
		contents += new BoxPanel( Orientation.Horizontal ) {
			contents += new Label("Адрес вещания")
			contents += sinkURLInput
			contents += new Button {
				text = "Установить"
				reactions += {
					case e: ButtonClicked => streamer ! new StreamingURLChange(sinkURLInput.text)
				}
			}
		}
		contents += new BoxPanel( Orientation.Horizontal ) {
			contents += chooseCapturingRegion
			contents += toggleStreaming
			contents += statusIndicator
		}
	}
	listenTo(chooseCapturingRegion, toggleStreaming)
	reactions += {
		case ButtonClicked(`chooseCapturingRegion`) =>
			val capturer = new CapturingFrame(region)
			listenTo(capturer)
			reactions += {
				case CaptureRegionChange(newRegion) =>
					region = newRegion
					streamer ! new CaptureRegionChange(region)
					toggleStreaming.enabled = true
			}
		case ButtonClicked(`toggleStreaming`) =>
			streamer ! new StreamingStateChange(toggleStreaming.selected)
	}

	streamer.start
}