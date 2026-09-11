package com.coeurrt.mhrecorder.detection;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.awt.*;


public class WindowsGameLocator {
    private static final String GAME_WINDOW_NAME = "Monster Hunter Wilds";
    private final User32 user32;

    public WindowsGameLocator() {
        this.user32 = User32.INSTANCE;
    }

    public Rectangle getGameWindowBounds() {
        WinDef.HWND hwnd = user32.FindWindow(null, GAME_WINDOW_NAME);
        if (hwnd == null) {
            throw new IllegalStateException("Monster Hunter Wilds window not found");
        }
        WinDef.RECT winRect = new WinDef.RECT();
        boolean success = user32.GetWindowRect(hwnd, winRect);
        if (!success) {
            throw new IllegalStateException("Unable to get game window bounds");
        }
        return new Rectangle(
                winRect.left, winRect.top, winRect.right - winRect.left, winRect.bottom - winRect.top);
    }
}
