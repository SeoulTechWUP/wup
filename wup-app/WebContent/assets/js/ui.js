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

class DropdownMenu {
    constructor() {
        var elem = document.createElement('div');

        /** @type {[HTMLDivElement]} */
        var itemElements = [];

        function addItem(title) {
            var e = document.createElement('div');
            e.className = 'dropdown-item disabled';
            e.textContent = title;
            e.onclick = emptyClickHandler;
            itemElements.push(e);
            elem.appendChild(e);
        }

        function addSeparator() {
            var e = document.createElement('div');
            e.className = 'dropdown-separator';
            e.onclick = emptyClickHandler;
            elem.appendChild(e);
        }

        function emptyClickHandler(e) {
            e.stopPropagation();
        }

        for (var i = 0; i < arguments.length; i++) {
            if (arguments[i] === '---') {
                addSeparator();
            }
            else {
                addItem(arguments[i]);
            }
        }

        elem.className = 'dropdown-menu';
        this.element = elem;

        this.itemClicked = function (itemIndex, handler) {
            var elem = itemElements[itemIndex];

            if (handler) {
                elem.onclick = function (e) {
                    handler.call(this, e);
                    dropdownManager.closeAll();
                    e.stopPropagation();
                };

                elem.classList.remove('disabled');
            }
            else {
                elem.onclick = emptyClickHandler;

                elem.classList.add('disabled');
            }
        };
    }
}

window.addEventListener("load", e => {
    let modalContainer = document.getElementById("modal-container");
    let modalFader = modalContainer.getElementsByClassName("fader")[0];
    let modalContentsArea = modalContainer.getElementsByClassName("contents")[0];
    let dropdownContainer = document.getElementById("dropdown-container");

    window.modalManager = {
        start: modalObj => {
            modalContainer.style.display = "block";

            setTimeout(() => {
                modalFader.style.opacity = "1";
                modalContentsArea.appendChild(modalObj.element);
            }, 10);
        },
        end: function () {
            modalContentsArea.innerHTML = '';
            modalFader.style.opacity = '0';
            setTimeout(function () {
                modalContainer.style.display = 'none';
            }, 550);
        }
    };

    window.dropdownManager = {
        show: function (x, y, menu) {
            this.closeAll();

            var elem = menu.element;

            dropdownContainer.appendChild(elem);

            var scrWidth = window.innerWidth;
            var scrHeight = window.innerHeight;
            var menuWidth = parseFloat(window.getComputedStyle(elem).width);
            var menuHeight = parseFloat(window.getComputedStyle(elem).height);
            var newX = Math.min(scrWidth - menuWidth - 5, Math.max(5, x));
            var newY = Math.min(scrHeight - menuHeight - 5, Math.max(5, y));

            elem.style.top = newY + 'px';
            elem.style.left = newX + 'px';
        },
        closeAll: function () {
            while (dropdownContainer.firstChild) {
                dropdownContainer.removeChild(dropdownContainer.firstChild);
            }
        }
    };

    document.body.addEventListener("click", e => {
        dropdownManager.closeAll();
    })
}, false);
