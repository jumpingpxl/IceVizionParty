package de.icevizion.partysystem.cloud.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Nico (JumpingPxl) Middendorf
 */

public class ScheduledTask {

	private Timer timer = new Timer();
	private Runnable runnable;
	private boolean started;

	public ScheduledTask(Runnable runnable) {
		this.runnable = runnable;
	}

	public ScheduledTask delay(long time, TimeUnit timeUnit) {
		if (!started) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					runnable.run();
				}
			}, timeUnit.toMillis(time));
			started = true;
		}
		return this;
	}

	public ScheduledTask repeat(long time, TimeUnit timeUnit) {
		if (!started) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					runnable.run();
				}
			}, timeUnit.toMillis(time), timeUnit.toMillis(time));
			started = true;
		}
		return this;
	}

	public ScheduledTask perform() {
		runnable.run();
		return this;
	}

	public void cancel() {
		if(started) {
			timer.cancel();
		}

		started = false;
	}
}