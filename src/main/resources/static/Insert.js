
    const typeSelect = document.getElementById('typPodnikuSelect');
    const nameSelect = document.getElementById('podnikSelect');
    const allOptions = Array.from(nameSelect.options);

    typeSelect.addEventListener('change', function() {
        const selectedTypeId = this.value; // ID vybraného typu (např. 1)
        nameSelect.innerHTML = '';
        
        allOptions.forEach(option => {
            const optionTypeId = option.getAttribute('data-type');
            // Pokud je typ nevybrán, nebo se shoduje ID typu u podniku, nebo je to prázdná volba
            if (selectedTypeId === "" || optionTypeId === selectedTypeId || option.value === "") {
                nameSelect.appendChild(option);
            }
        });
    });

    nameSelect.addEventListener('change', function() {
        const selectedOption = this.options[this.selectedIndex];
        const typeIdOfPodnik = selectedOption.getAttribute('data-type');
        
        if (typeIdOfPodnik) {
            typeSelect.value = typeIdOfPodnik;
        }
    });