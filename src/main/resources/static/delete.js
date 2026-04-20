function toggleReviewMenu(button) {
    const dropdown = button.nextElementSibling;
    dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
}

function deleteReview(idObsahu) {
    if (confirm('Opravdu chcete tuto recenzi smazat?')) {
        // Zavoláme Java backend pomocí POST požadavku
        fetch('/review/' + idObsahu + '/delete', {
            method: 'POST',
            // Přidáme CSRF token, který Spring Security vyžaduje u POST požadavků
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            }
        }).then(response => {
            if (response.ok) {
                // Pokud se to povedlo, skryjeme recenzi na stránce
                location.reload(); // Pro jednoduchost stránku obnovíme
            } else {
                alert('Chyba při mazání recenze.');
            }
        });
    }
}