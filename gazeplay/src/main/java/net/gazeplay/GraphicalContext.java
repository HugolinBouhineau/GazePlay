package net.gazeplay;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.gazeplay.commons.ui.I18NButton;
import net.gazeplay.commons.ui.I18NTooltip;

@Slf4j
public abstract class GraphicalContext<T extends Parent> {

    @Getter
    private final GazePlay gazePlay;

    @Getter
    protected final T root;

    @Getter
    private final MusicControl musicControl;

    GraphicalContext(GazePlay gazePlay, T root) {
        this.gazePlay = gazePlay;
        this.root = root;
        this.musicControl = new MusicControl(gazePlay);
    }

    public void setUpOnStage(final Scene scene) {
        // Make sure we are the root of the scene
        scene.setRoot(root);

        musicControl.updateMusicControler();

        log.debug("Finished setup stage with the game scene");
    }

    public abstract ObservableList<Node> getChildren();

    public void clear() {
        getChildren().clear();
        log.warn("Nodes not removed: {}", getChildren().size());
    }

    I18NButton createToggleFullScreenButtonInGameScreen(@NonNull GazePlay gazePlay) {
        EventHandler<Event> eventHandler = e -> gazePlay.toggleFullScreen();

        I18NButton button = new I18NButton(gazePlay.getTranslator(), (String[]) null);
        configureFullScreenToggleButton(gazePlay.isFullScreen(), button);

        gazePlay.getFullScreenProperty().addListener((observable, wasFullScreen, isFullScreen) -> configureFullScreenToggleButton(isFullScreen, button));

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);

        return button;
    }

    private void configureFullScreenToggleButton(Boolean isFullScreen, I18NButton button) {
        final Image buttonGraphics;
        final String label;
        if (isFullScreen) {
            buttonGraphics = new Image("data/common/images/fullscreen-exit.png");
            label = "Exit FullScreen";
        } else {
            buttonGraphics = new Image("data/common/images/fullscreen-enter.png");
            label = "Enter FullScreen";
        }
        ImageView imageView = new ImageView(buttonGraphics);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(Screen.getPrimary().getBounds().getWidth() / 40);
        button.heightProperty().addListener((observable, oldValue, newValue) -> imageView.setFitHeight(newValue.doubleValue() / 2d));
        button.setGraphic(imageView);
        button.setTooltip(new I18NTooltip(gazePlay.getTranslator(), label));
    }

}
