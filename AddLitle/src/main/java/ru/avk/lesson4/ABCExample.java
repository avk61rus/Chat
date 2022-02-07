package ru.avk.lesson4;

import java.security.AllPermission;

public class ABCExample {

    private final Object mon = new Object();
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {
        ABCExample waitNotifyObj = new ABCExample();
        Thread trA = new Thread(waitNotifyObj::printA);
        Thread trB = new Thread(waitNotifyObj::printB);
        Thread trC = new Thread(waitNotifyObj::printC);
        trA.start();
        trB.start();
        trC.start();
    }

    public void printA() {
        synchronized (mon) {
            for (int i =0; i < 5; i++) {
                while (currentLetter != 'A') {
                    try {
                        mon.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print("A");
                currentLetter = 'B';
                mon.notifyAll();
            }
        }
    }

    public void printB() {
        synchronized (mon) {
            for (int i =0; i < 5; i++) {
                while (currentLetter != 'B') {
                    try {
                        mon.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print("B");
                currentLetter = 'C';
                mon.notifyAll();
            }
        }
    }

    public void printC() {
        synchronized (mon) {
            for (int i =0; i < 5; i++) {
                while (currentLetter != 'C') {
                    try {
                        mon.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print("C");
                currentLetter = 'A';
                mon.notifyAll();
            }
        }
    }

}
