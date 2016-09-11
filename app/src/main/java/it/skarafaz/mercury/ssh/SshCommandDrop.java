package it.skarafaz.mercury.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshCommandDrop<E> {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandDrop.class);
    private E obj;
    private boolean empty = true;

    public synchronized E take() {
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage().replace("\n", " "));
            }
        }
        empty = true;
        notifyAll();
        return obj;
    }

    public synchronized void put(E obj) {
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage().replace("\n", " "));
            }
        }
        empty = false;
        this.obj = obj;
        notifyAll();
    }
}
