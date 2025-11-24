# Regeneration

The `RegenAuthoritySystem` handles the natural healing of entities including players, NPCs, and even blocks.

To activate regeneration send `ActivateRegenEvent(String id, float value, float endTime)` for a specific entity.
This will result in health being regenerated for the specified entity every second. 
Sending an empty event `ActivateRegenEvent()` activates the base regeneration for the specified entity.

To deactivate a particular type of regeneration, send `DeactivateRegenEvent(String id)`. 
Sending an empty event `DeactivateRegenEvent()` deactivates the base regeneration for the specified entity.