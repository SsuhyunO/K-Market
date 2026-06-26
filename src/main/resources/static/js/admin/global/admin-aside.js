document.addEventListener('DOMContentLoaded', function () {
    const navItems = [...document.querySelectorAll('.aside-nav .nav-item')];

    openCurrentMenu(navItems);

    navItems.forEach(item => {
        const link = item.querySelector(':scope > .nav-link');
        const subMenu = item.querySelector(':scope > .sub-menu');
        if (!link || !subMenu) return;

        link.setAttribute('aria-expanded', item.classList.contains('open'));

        link.addEventListener('click', event => {
            event.preventDefault();

            const willOpen = !item.classList.contains('open');
            navItems.forEach(other => setMenuOpen(other, false));
            setMenuOpen(item, willOpen);
        });
    });
});

function openCurrentMenu(navItems) {
    const currentPath = window.location.pathname;
    const currentItem = navItems.find(item =>
        [...item.querySelectorAll('.sub-menu a')]
            .some(link => new URL(link.href, window.location.origin).pathname === currentPath)
    );

    if (currentItem) setMenuOpen(currentItem, true);
}

function setMenuOpen(item, isOpen) {
    item.classList.toggle('open', isOpen);
    item.querySelector(':scope > .nav-link')?.setAttribute('aria-expanded', isOpen);
}
