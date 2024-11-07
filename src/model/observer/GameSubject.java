package model.observer;

/**
 * GameSubject interface defines methods for attaching and detaching observers
 * This is implemented by the Model to allow observers to register for notifications
 */
public interface GameSubject {
  /**
   * Attach an observer to receive notifications
   *
   * @param observer the observer to attach
   */
  void addObserver(GameObserver observer);

  /**
   * Detach an observer to stop receiving notifications
   *
   * @param observer the observer to detach
   */
  void removeObserver(GameObserver observer);

  /**
   * Notify all observers about state changes
   */
  void notifyObservers();
}