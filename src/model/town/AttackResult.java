package model.town;

/**
 * Represents the result of an attack attempt on the target character.
 * Encapsulates whether the attack was successful, any message about the attack,
 * and whether the attack defeated the target.
 */
public class AttackResult {
  private final boolean success;
  private final String message;
  private final boolean targetDefeated;
  private final int damageDealt;

  /**
   * Creates a new AttackResult for unsuccessful attacks.
   *
   * @param message Description of why the attack failed
   */
  public AttackResult(String message) {
    this(false, message, false, 0);
  }

  /**
   * Creates a new AttackResult with full details.
   *
   * @param success        Whether the attack succeeded
   * @param message        Description of the attack result
   * @param targetDefeated Whether the target was defeated
   * @param damageDealt    Amount of damage dealt
   */
  public AttackResult(boolean success, String message, boolean targetDefeated, int damageDealt) {
    this.success = success;
    this.message = message;
    this.targetDefeated = targetDefeated;
    this.damageDealt = damageDealt;
  }

  /**
   * Checks if the attack was successful.
   *
   * @return true if the attack succeeded, false otherwise
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Gets the message describing the attack result.
   *
   * @return Description of the attack result
   */
  public String getMessage() {
    return message;
  }

  /**
   * Checks if the target was defeated by this attack.
   *
   * @return true if the target was defeated, false otherwise
   */
  public boolean isTargetDefeated() {
    return targetDefeated;
  }

  /**
   * Gets the amount of damage dealt by the attack.
   *
   * @return Amount of damage dealt
   */
  public int getDamageDealt() {
    return damageDealt;
  }
}