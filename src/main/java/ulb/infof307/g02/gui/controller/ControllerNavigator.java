package ulb.infof307.g02.gui.controller;

import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This interface is used by all the controllers to "go" back and forth between different view. In reality, it displays
 * the "next view", i.e. the one associated to the controller given in argument.
 */
public interface ControllerNavigator {

    /**
     * Go to the next "view/controller" association, this one is used for controllers that does not require a model.
     * @param nextController : the constructor method of the controller associated to the view we want to display
     * @param <T> we ensure that the constructor is associated to a controller ("supplies a controller")
     */
    <T extends Controller> void goTo(Supplier<T> nextController);

    /**
     * Go to the next "view/controller" association, this one is used when the controller require a model to display
     * in the view. For example, when modifying a recipe
     * @param nextController : the constructor method of the controller associated to the view we want to display
     * @param model : the model needed by the controller to properly display information or execute
     * @param <M> we ensure that we receive a SQLModel, letting us use SQLModel methods such as getId() or getName()
     * @param <T> we ensure that the constructor is associated to a controller ("supplies a controller when given a SQLModel in argument")
     */
    <M extends SQLModel<M>, T extends Controller> void goTo(Function<M, T> nextController, M model);

}
