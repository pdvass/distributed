package distributed.Server;

/**
 * Enum of HandlerTypes. Every type must have its own handler.
 */
public enum HandlerTypes {
    CLIENT,
    WORKER,
    MANAGER,
    BOOKKEEPER,
    REDUCER
}
