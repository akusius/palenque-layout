package hu.akusius.palenque.layout.util;

import java.util.EventObject;

/**
 * Generikus interfész az események egyszerűbb kezeléséhez.
 * @param <T> Az Event objektum típusa.
 * @author Bujdosó Ákos
 */
public interface EventListener<T extends EventObject> extends java.util.EventListener {

  /**
   * Megtörtént az esemény.
   * @param e Az Event objektum az eseményhez.
   */
  void notify(T e);
}
