package h359.kvergjelme.client

import swing.event._
import java.awt.{Rectangle}

case class StreamingStateChange(state: Boolean) extends Event
case class CaptureRegionChange(region: Rectangle) extends Event