package br.com.horizon.ui;

public class VisualComponents {
    private Boolean appBar;
    private Boolean bottomNavigation;

    public VisualComponents() {
        this.appBar = true;
    }

    public VisualComponents(Boolean appBar, Boolean bottomNavigation) {
        this.appBar = appBar;
        this.bottomNavigation = bottomNavigation;
    }

    public Boolean hasAppBar() {
        return appBar;
    }

    public Boolean hasBottomNav() {
        return bottomNavigation;
    }

    public void setAppBar(Boolean appBar) {
        this.appBar = appBar;
    }

    public void setBottomNavigation(Boolean bottomNavigation) {
        this.bottomNavigation = bottomNavigation;
    }
}
