package de.nivram710.whatsLeft.ui.onBoarding;

class ScreenItem {
    private String Title,Description;
    private int ScreenImg;

    ScreenItem(String title, String description, int screenImg) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
    }

    String getTitle() {
        return Title;
    }

    String getDescription() {
        return Description;
    }

    int getScreenImg() {
        return ScreenImg;
    }
}
