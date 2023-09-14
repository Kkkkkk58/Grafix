package ru.itmo.grafix;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class ShortcutRegisterService {

    public static void registerShortcuts(Scene scene, MainSceneController controller) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED,
                createShortcutEvent(controller::saveFile, KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        scene.addEventHandler(KeyEvent.KEY_PRESSED,
                createShortcutEvent(controller::saveFileAs, KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        scene.addEventHandler(KeyEvent.KEY_PRESSED,
                createShortcutEvent(controller::openFile, KeyCode.O, KeyCombination.SHORTCUT_DOWN));
    }

    private static EventHandler<? super KeyEvent> createShortcutEvent(Runnable method, KeyCode code, KeyCombination.Modifier... combinations) {
        KeyCombination saveCombination = new KeyCodeCombination(code, combinations);
        return event -> {
            if (saveCombination.match(event)) {
                method.run();
            }
        };
    }
}
