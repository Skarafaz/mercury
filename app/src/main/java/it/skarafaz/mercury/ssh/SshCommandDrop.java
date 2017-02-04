/*
 * Mercury-SSH
 * Copyright (C) 2017 Skarafaz
 *
 * This file is part of Mercury-SSH.
 *
 * Mercury-SSH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Mercury-SSH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mercury-SSH.  If not, see <http://www.gnu.org/licenses/>.
 */

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
