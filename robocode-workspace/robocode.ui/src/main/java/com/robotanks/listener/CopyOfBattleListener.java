/*package com.robotanks.listener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Timer;

import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.battle.snapshot.RobotSnapshot; //not needed
import net.sf.robocode.io.Logger;
//import com.robotanks.listener.BattleListener.BattleObserver;
//import com.robotanks.BattleListener.TimerTask;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.BattlePausedEvent;
import robocode.control.events.BattleResumedEvent;
import robocode.control.events.BattleStartedEvent;
import robocode.control.events.IBattleListener;
import robocode.control.events.RoundEndedEvent;
import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.events.TurnStartedEvent;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import java.io.DataOutputStream;
import java.net.*;

public final class CopyOfBattleListener implements IBattleListener {

	private final IBattleManager battleManager;
	private final BattleEventDispatcher battleEventDispatcher = new BattleEventDispatcher();
	private final BattleObserver observer;
	private final Timer timerTask;

	private final AtomicReference<ITurnSnapshot> snapshot;
	private final AtomicBoolean isRunning;
	private final AtomicBoolean isPaused;
	private final AtomicInteger majorEvent;
	private final AtomicInteger lastMajorEvent;
	private ITurnSnapshot lastSnapshot;
	private StringBuilder[] outCache;

	public CopyOfBattleListener(IBattleManager battleManager, int maxFps, boolean skipSameFrames) {
		this.battleManager = battleManager;
		snapshot = new AtomicReference<ITurnSnapshot>(null);

		this.skipSameFrames = skipSameFrames;
		timerTask = new Timer(1000 / maxFps, new TimerTask());
		isRunning = new AtomicBoolean(false);
		isPaused = new AtomicBoolean(false);
		majorEvent = new AtomicInteger(0);
		lastMajorEvent = new AtomicInteger(0);
		
		new Server();

		observer = new BattleObserver();
		battleManager.addListener(observer);
	
	}

	protected void finalize() throws Throwable {
		try {
			timerTask.stop();
			battleManager.removeListener(observer);
		} finally {
			super.finalize();
		}
	}

	public synchronized void addListener(IBattleListener listener) {
		battleEventDispatcher.addListener(listener);
	}

	public synchronized void removeListener(IBattleListener listener) {
		battleEventDispatcher.removeListener(listener);
	}

	public ITurnSnapshot getLastSnapshot() {
		return lastSnapshot;
	}

	// this is always dispatched on AWT thread
	private void rtOnTurnEnded(boolean forceRepaint, boolean readoutText) {
		try {
			ITurnSnapshot current = snapshot.get();

			if (current == null) { // !isRunning.get() ||
				// paint logo
				lastSnapshot = null;
				battleEventDispatcher.onTurnEnded(new TurnEndedEvent(null));
			} else {
				if (lastSnapshot != current || !skipSameFrames || forceRepaint) {
					lastSnapshot = current;

					IRobotSnapshot[] robots = null;

					if (readoutText) {
						synchronized (snapshot) {
							robots = lastSnapshot.getRobots();

							for (int i = 0; i < robots.length; i++) {
								RobotSnapshot robot = (RobotSnapshot) robots[i];

								final StringBuilder cache = outCache[i];

								if (cache.length() > 0) {
									robot.setOutputStreamSnapshot(cache.toString());
									outCache[i].setLength(0);
								}
							}
						}
					}

					battleEventDispatcher.onTurnEnded(new TurnEndedEvent(lastSnapshot));

					if (readoutText) {
						for (IRobotSnapshot robot : robots) {
							((RobotSnapshot) robot).setOutputStreamSnapshot(null);
						}
					}

					calculateFPS();
				}
			}
		} catch (Throwable t) {
			Logger.logError(t);
		}
	}

	public int getFPS() {
		return fps;
	}

	// FPS (frames per second) calculation
	private int fps;
	private long measuredFrameCounter;
	private long measuredFrameStartTime;
	private final boolean skipSameFrames;

	private void calculateFPS() {
		// Calculate the current frames per second (FPS)

		if (measuredFrameCounter++ == 0) {
			measuredFrameStartTime = System.nanoTime();
		}

		long deltaTime = System.nanoTime() - measuredFrameStartTime;

		if (deltaTime / 1000000000 >= 1) {
			fps = (int) (measuredFrameCounter * 1000000000L / deltaTime);
			measuredFrameCounter = 0;
		}
	}

	private class TimerTask implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			rtOnTurnEnded(false, true);
		}
	}


	// BattleObserver methods are always called by battle thread
	// but everything inside invokeLater {} block in on AWT thread 
	private class BattleObserver extends BattleAdaptor {

		public void onTurnEnded(final TurnEndedEvent event) {
			if (lastMajorEvent.get() == majorEvent.get()) {
				// snapshot is updated out of order, but always within the same major event
				snapshot.set(event.getTurnSnapshot());
			}

			final IRobotSnapshot[] robots = event.getTurnSnapshot().getRobots();

			for (int i = 0; i < robots.length; i++) {
				RobotSnapshot robot = (RobotSnapshot) robots[i];
				final int r = i;
				final String text = robot.getOutputStreamSnapshot();

				if (text != null && text.length() != 0) {
					robot.setOutputStreamSnapshot(null);
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							synchronized (snapshot) {
								outCache[r].append(text);
							}
						}
					});
				}
			}
			if (isPaused.get()) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						rtOnTurnEnded(false, true);
					}
				}
				); 
			}
		}

		public void onRoundStarted(final RoundStartedEvent event) {
			if (lastMajorEvent.get() == majorEvent.get()) {
				snapshot.set(event.getStartSnapshot());
			}
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					rtOnTurnEnded(true, false);
					battleEventDispatcher.onRoundStarted(event);
					lastMajorEvent.incrementAndGet();
				}
			});
		}

		public void onBattleStarted(final BattleStartedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					isRunning.set(true);
					isPaused.set(false);
					synchronized (snapshot) {
						outCache = new StringBuilder[event.getRobotsCount()];
						for (int i = 0; i < event.getRobotsCount(); i++) {
							outCache[i] = new StringBuilder(1024);
						}
					}
					snapshot.set(null);
					battleEventDispatcher.onBattleStarted(event);
					lastMajorEvent.incrementAndGet();
					rtOnTurnEnded(true, false);
					timerTask.start();
				}
			});
		}

		public void onBattleFinished(final BattleFinishedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					isRunning.set(false);
					isPaused.set(false);
					timerTask.stop();
					// flush text cache
					rtOnTurnEnded(true, true);

					battleEventDispatcher.onBattleFinished(event);
					lastMajorEvent.incrementAndGet();
					snapshot.set(null);

					// paint logo
					rtOnTurnEnded(true, true);
				}
			});
		}

		public void onBattleCompleted(final BattleCompletedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleCompleted(event);
					lastMajorEvent.incrementAndGet();
					rtOnTurnEnded(true, true);
				}
			});
		}

		public void onRoundEnded(final RoundEndedEvent event) {
			majorEvent.incrementAndGet();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onRoundEnded(event);
					lastMajorEvent.incrementAndGet();
					rtOnTurnEnded(true, true);
				}
			});
		}

		public void onBattlePaused(final BattlePausedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					timerTask.stop();
					battleEventDispatcher.onBattlePaused(event);
					rtOnTurnEnded(true, true);
					isPaused.set(true);
				}
			});
		}

		public void onBattleResumed(final BattleResumedEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleResumed(event);
					if (isRunning.get()) {
						timerTask.start();
						isPaused.set(false);
					}
				}
			});
		}

		public void onBattleMessage(final BattleMessageEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleMessage(event);
				}
			});
		}

		public void onBattleError(final BattleErrorEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					battleEventDispatcher.onBattleError(event);
				}
			});
		}

		public void onTurnStarted(TurnStartedEvent event) {
			// TODO Auto-generated method stub
			
		}

	}

	public class Server {
		   public Server()  {
		      String data = "Toobie ornaught toobie";
		      try {
		         ServerSocket srvr = new ServerSocket(1234);
		         Socket skt = srvr.accept();
		         Logger.logMessage("Server has connected", false);
		         DataOutputStream out = new DataOutputStream(skt.getOutputStream());
		         Logger.logMessage("Sending string", false);
		         out.writeBytes(data);
		         out.close();
		         skt.close();
		         srvr.close();
		      }
		      catch(Exception e) {
			        Logger.logMessage("Error", false);
			        System.out.println("Hello, Earthling");
		      }
	           
	                 
		   }
		}

	public void onBattleStarted(BattleStartedEvent event) {
		// TODO Auto-generated method stub
	}

	public void onBattleFinished(BattleFinishedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onBattleCompleted(BattleCompletedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onBattlePaused(BattlePausedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onBattleResumed(BattleResumedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onRoundStarted(RoundStartedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onRoundEnded(RoundEndedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onTurnStarted(TurnStartedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onTurnEnded(TurnEndedEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onBattleMessage(BattleMessageEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void onBattleError(BattleErrorEvent event) {
		// TODO Auto-generated method stub
		
	}
}*/