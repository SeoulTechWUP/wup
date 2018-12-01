class TopTabs {
    constructor(baseElement) {
        var that = this;

        /** @type {HTMLElement} */
        var elem = baseElement;

        this.tabs = elem.getElementsByClassName('tab');
        this.currentTab = 0;
        this.element = elem;

        for (var i = 0; i < this.tabs.length; i++) {
            (function (index) {
                that.tabs[index].onclick = function (e) {
                    that.setTab(index);
                };
            })(i);
        }

        /** @type {(tabIndex: number) => void} */
        this.tabChanged = null;
    }

    setTab(index) {
        this.currentTab = index;

        if (this.tabChanged) {
            this.tabChanged.call(this, this.currentTab);
        }

        this.updateUI();
    }

    updateUI() {
        for (var i = 0; i < this.tabs.length; i++) {
            this.tabs[i].classList.remove("active");
        }

        this.tabs[this.currentTab].classList.add("active");
    }
}
