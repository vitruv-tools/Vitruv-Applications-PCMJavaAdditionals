package tools.vitruv.applications.pcmjava.integrationFromGit.test;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
     * Thread-safe simple progress monitor for knowing when a job is done.
     *
     */
    public class DoneFlagProgressMonitor extends NullProgressMonitor {

        //private /*final*/ AtomicBoolean isDone = new AtomicBoolean(false);
    	private boolean isDone = false;

        @Override
        public synchronized void done() {
            this.isDone = true;
            this.notifyAll();
        }

        public synchronized boolean isDone() {
        	while(!this.isDone) {
        		try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	return this.isDone;
        }

    }