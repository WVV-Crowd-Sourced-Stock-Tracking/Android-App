package de.whatsLeft.ui.onBoarding;

/**
 * Class to store all screen items
 *
 * @since 1.0.0
 * @author Chris de Machaut
 * @version 1.0
 */
class ScreenItem {
    private String title, description;
    private int screenImg;

    ScreenItem(String title, String description, int screenImg) {
        this.title = title;
        this.description = description;
        this.screenImg = screenImg;
    }

    /**
     * @return title Title for current on boarding screen
     * @since 1.0.0
     */
    String getTitle() {
        return title;
    }

    /**
     * @return description Description that is displayed on current on boarding screen
     * @since 1.0.0
     */
    String getDescription() {
        return description;
    }

    /**
     * @return resourceID ResourceID of current image that is displayed in current on boarding screen
     * @since 1.0.0
     */
    int getScreenImg() {
        return screenImg;
    }
}
