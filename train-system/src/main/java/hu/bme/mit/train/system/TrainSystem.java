package hu.bme.mit.train.system;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import hu.bme.mit.train.controller.TrainControllerImpl;
import hu.bme.mit.train.interfaces.TrainController;
import hu.bme.mit.train.interfaces.TrainSensor;
import hu.bme.mit.train.interfaces.TrainUser;
import hu.bme.mit.train.sensor.TrainSensorImpl;
import hu.bme.mit.train.user.TrainUserImpl;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class TrainSystem implements AutoCloseable {

	private ActorSystem system = ActorSystem.create("Main");

	private TrainController controller = TypedActor.get(system)
			.typedActorOf(new TypedProps<TrainController>(TrainController.class, () -> new TrainControllerImpl()));

	private TrainUser user = TypedActor.get(system)
			.typedActorOf(new TypedProps<TrainUser>(TrainUser.class, () -> new TrainUserImpl(controller)));

	private TrainSensor sensor = TypedActor.get(system)
			.typedActorOf(new TypedProps<TrainSensor>(TrainSensor.class, () -> new TrainSensorImpl(controller)));

	public TrainController getController() {
		return controller;
	}

	public TrainSensor getSensor() {
		return sensor;
	}

	public TrainUser getUser() {
		return user;
	}

	@Override
	public void close() throws Exception {
		Future<Terminated> terminated = system.terminate();
		Await.result(terminated, Duration.create(5, TimeUnit.SECONDS));
	}

}