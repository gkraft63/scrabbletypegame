package jscrabble;
/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/**
 * Internal class to manage all Timers using one thread.
 * TimerQueue manages a queue of Timers. The Timers are chained
 * together in a linked list sorted by the order in which they will expire.
 *
 * @version 1.31 02/06/02
 * @author Dave Moore
 */
final class TimerQueue implements Runnable {
    
    private static final TimerQueue sharedInstance = new TimerQueue();
    
    Timer   firstTimer;
    boolean running;
    
    /* Lock object used in place of class object for synchronization.
     * (4187686)
     */
    private static final Object classLock = new Object();
    
    /**
     * Constructor for TimerQueue.
     */
    public TimerQueue() {
        super();
    }
    
    public static TimerQueue sharedInstance() {
        synchronized(classLock) {
            if(!sharedInstance.running)
                sharedInstance.start();
        }
        return sharedInstance;
    }
    
    synchronized void start() {
        if(running)
            throw new RuntimeException("Can't start a TimerQueue that is already running");
        else {
            Thread timerThread = new Thread(this, "Scrabble.TimerQueue");
            try {
                timerThread.setDaemon(true);
            }
            catch (SecurityException e) {}
            timerThread.setPriority(Thread.NORM_PRIORITY);
            timerThread.start();
            running = true;
        }
    }
    
    synchronized void stop() {
        running = false;
        notify();
    }
    
    synchronized void addTimer(Timer timer, long expirationTime) {
        Timer previousTimer;
        Timer nextTimer;
        
        // If the Timer is already in the queue, then ignore the add.
        if(timer.running)
            return;
        
        previousTimer = null;
        nextTimer = firstTimer;
        
        // Insert the Timer into the linked list in the order they will
        // expire.  If two timers expire at the same time, put the newer entry
        // later so they expire in the order they came in.
        
        while(nextTimer != null) {
            if(nextTimer.expirationTime > expirationTime)
                break;
            
            previousTimer = nextTimer;
            nextTimer = nextTimer.nextTimer;
        }
        
        if(previousTimer == null)
            firstTimer = timer;
        else
            previousTimer.nextTimer = timer;
        
        timer.expirationTime = expirationTime;
        timer.nextTimer = nextTimer;
        timer.queued = true;
        timer.running = true;
        notify();
    }
    
    synchronized void removeTimer(Timer timer) {
        Timer   previousTimer;
        Timer   nextTimer;
        boolean found;
        
        if(!timer.running)
            return;
        
        previousTimer = null;
        nextTimer = firstTimer;
        found = false;
        
        while(nextTimer != null) {
            if(nextTimer == timer) {
                found = true;
                break;
            }
            
            previousTimer = nextTimer;
            nextTimer = nextTimer.nextTimer;
        }
        
        if(!found)
            return;
        
        if(previousTimer == null)
            firstTimer = timer.nextTimer;
        else
            previousTimer.nextTimer = timer.nextTimer;
        
        timer.expirationTime = 0;
        timer.nextTimer = null;
        timer.queued = false;
        timer.running = false;
    }
    
    synchronized boolean containsTimer(Timer timer) {
        return timer.running;
    }
    
    /**
     * If there are a ton of timers, this method may never return.  It loops
     * checking to see if the head of the Timer list has expired.  If it has,
     * it posts the Timer and reschedules it if necessary.
     */
    synchronized long postExpiredTimers() {
        Timer   timer;
        long    currentTime;
        long    timeToWait;
        
        // The timeToWait we return should never be negative and only be zero
        // when we have no Timers to wait for.
        
        do {
            timer = firstTimer;
            if(timer == null)
                return 0;
            
            currentTime = System.currentTimeMillis();
            timeToWait = timer.expirationTime - currentTime;
            
            if(timeToWait <= 0) {
                timer.queued = false;
                try {
                    timer.post();  // have timer post an event
                } catch (Exception e) {
                    System.err.println("Exception occurred during timer posting an event:");
                    e.printStackTrace();
                }
                
                if(timer.running && !timer.queued) {
                    
                    // Remove the timer from the queue
                    removeTimer(timer);
                    
                    // This tries to keep the interval uniform at
                    // the cost of drift.
                    if(timer.isRepeats())
                        addTimer(timer, currentTime + timer.getDelay());
                }
            }
            
            // Allow other threads to call addTimer() and removeTimer()
            // even when we are posting Timers like mad.  Since the wait()
            // releases the lock, be sure not to maintain any state
            // between iterations of the loop.
            
            try {
                wait(1);
            } catch(InterruptedException e) {
            }
        } while(timeToWait <= 0);
        
        return timeToWait;
    }
    
    public synchronized void run() {
        long timeToWait;
        
        try {
            while(running) {
                timeToWait = postExpiredTimers();
                try {
                    wait(timeToWait);
                } catch(InterruptedException e) {
                }
            }
        } catch(ThreadDeath td) {
            running = false;
            // Mark all the timers we contain as not being queued.
            Timer timer = firstTimer;
            while(timer != null) {
                timer.running = false;
                timer.queued = false;
                timer = timer.nextTimer;
            }
            throw td;
        }
    }
    
    
    public synchronized String toString() {
        StringBuffer buf;
        Timer nextTimer;
        
        buf = new StringBuffer();
        buf.append("TimerQueue (");
        
        nextTimer = firstTimer;
        while (nextTimer != null) {
            buf.append(nextTimer.toString());
            
            nextTimer = nextTimer.nextTimer;
            if (nextTimer != null) buf.append(", ");
        }
        
        buf.append(")");
        return buf.toString();
    }
}
