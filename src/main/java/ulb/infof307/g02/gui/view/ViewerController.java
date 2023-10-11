package ulb.infof307.g02.gui.view;

/**
 * ViewerController is an interface implemented by view controllers, allow us to generalize this sort of controllers
 * and make other class implement it to have a custom type verification.
 *
 * @param <T> is the view controller own listener, permit generalizing the listener setter
 */
public abstract class ViewerController<T extends ViewerController.ViewerListener> {

    protected T listener;

    public void setListener(T listener) {
        this.listener = listener;
    }

    /**
     * Can be used to implement mandatory methods, here it is only used to make generics and custom type verifications
     */
    public interface ViewerListener {
        // Only used for extends and implements, permit generics
    }

}
