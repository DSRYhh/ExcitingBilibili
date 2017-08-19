package Actors.Messages

/**
  * Created by hyh on 2017/8/19.
  */
sealed trait ActorMessages

case object LaunchNewListMonitor extends ActorMessages
case object LaunchTravseral extends ActorMessages

case class AvList(avList : List[Int]) extends ActorMessages

case class UpdateVideo(av : Int) extends ActorMessages
case class InsertVideo(av : Int) extends ActorMessages

case object TaskFinish extends ActorMessages
