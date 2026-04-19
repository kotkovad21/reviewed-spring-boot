document.getElementById('load-more-btn').addEventListener('click', function() {
        const btn = this;
        const page = btn.getAttribute('data-page');

        fetch('/visits/load-more?page=' + page)
            .then(response => {
                if (!response.ok) throw new Error('Chyba při načítání');
                return response.text();
            })
            .then(html => {
                // Pokud už nejsou další recenze
                if (html.trim() === "") {
                    btn.innerHTML = "Všechny recenze načteny";
                    btn.disabled = true;
                    btn.style.opacity = "0.5";
                    btn.style.cursor = "default";
                    return;
                }

                // Vložení nových kartiček na konec gridu
                document.getElementById('recenze-container').insertAdjacentHTML('beforeend', html);
                btn.setAttribute('data-page', parseInt(page) + 1);
            })
            .catch(error => {
                console.error(error);
                btn.style.display = 'none';
            });
    });