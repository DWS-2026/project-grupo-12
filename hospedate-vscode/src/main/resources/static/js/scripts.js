/*!
* Start Bootstrap - Creative v7.0.7 (https://startbootstrap.com/theme/creative)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-creative/blob/master/LICENSE)
*/
//
// Scripts
// 


//Dynamic transparency of the top bar
// Navbar shrink function
// Detects when the page has finished loading
window.addEventListener('DOMContentLoaded', event => {
    // Changes the appearance of the navbar according to the scroll position
    var navbarShrink = function () {
        const navbarCollapsible = document.body.querySelector('#mainNav');
        if (!navbarCollapsible) {
            return;
        }
        // If we are at the very top (pixel 0), remove the solid background
        if (window.scrollY === 0) {
            navbarCollapsible.classList.remove('navbar-shrink')
        } else {
             // When going down, add the class that sets the background to white/orange
            navbarCollapsible.classList.add('navbar-shrink')
        }

    };
    // Initializes the state on load and listens for mouse movement
    // Shrink the navbar 
    navbarShrink();
    // Shrink the navbar when page is scrolled
    document.addEventListener('scroll', navbarShrink);

    // Activate Bootstrap scrollspy on the main nav element
    const mainNav = document.body.querySelector('#mainNav');
    if (mainNav) {
        new bootstrap.ScrollSpy(document.body, {
            target: '#mainNav',
            rootMargin: '0px 0px -40%',
        });
    };

    // Collapse responsive navbar when toggler is visible (mobiles)
    const navbarToggler = document.body.querySelector('.navbar-toggler');
    const responsiveNavItems = [].slice.call(
        document.querySelectorAll('#navbarResponsive .nav-link')
    );
    responsiveNavItems.map(function (responsiveNavItem) {
        responsiveNavItem.addEventListener('click', () => {
            if (window.getComputedStyle(navbarToggler).display !== 'none') {
                navbarToggler.click();
            }
        });
    });

    // Activate SimpleLightbox plugin for portfolio items
    new SimpleLightbox({
        elements: '#portfolio a.portfolio-box'
    });


});

// ============================================
// HOTEL GALLERY FUNCTIONALITY
// ============================================

/**
 * Opens the hotel gallery carousel with the clicked image
 * @param {HTMLElement} clickedImage - The image element that was clicked
 */
function openGallery(clickedImage) {
    // Get all gallery images from the DOM
    const galleryImages = document.querySelectorAll('.gallery-img');
    
    if (galleryImages.length === 0) {
        console.warn('Gallery images not found');
        return;
    }

    // Find the index of the clicked image
    let currentIndex = 0;
    galleryImages.forEach((img, index) => {
        if (img === clickedImage) {
            currentIndex = index;
        }
    });

    // Get the carousel container
    const contenedorCarrusel = document.getElementById('carousel-items-container');
    if (!contenedorCarrusel) {
        console.warn('Carousel container not found');
        return;
    }

    contenedorCarrusel.innerHTML = ''; // CLEAN

    // GENERATE THE DIAPOSITIVES
    galleryImages.forEach((img, index) => {
        const claseActive = index === currentIndex ? 'active' : '';
        const imgUrl = img.getAttribute('src');
        const itemHtml = `
            <div class="carousel-item ${claseActive}" style="height: 80vh;">
                <img src="${imgUrl}" class="d-block w-100 h-100" style="object-fit: contain;" alt="Foto ${index}">
            </div>
        `;
        contenedorCarrusel.innerHTML += itemHtml;
    });

    // Show the modal
    const myModal = new bootstrap.Modal(document.getElementById('modalGallery'));
    myModal.show();
    }

    //Function to avoid missed days in the hotel calendar
    window.addEventListener('DOMContentLoaded', event => {
        
        const entryInput = document.getElementById('entryDate');
        const departureInput = document.getElementById('departureDate');

        // We check if we are on the hotel page by checking if the inputs are present
        if (entryInput && departureInput) {
            
            // The entry date cannot be earlier than today.
            const today = new Date().toISOString().split('T')[0];
            entryInput.setAttribute('min', today);

            // 2. When the user selects an entry date...
            entryInput.addEventListener('change', function() {
                if(this.value) {
                    // We calculate that the departure date must be at least 1 day after the entry date
                    let minDeparture = new Date(this.value);
                    minDeparture.setDate(minDeparture.getDate() + 1);
                    
                    let minDepartureStr = minDeparture.toISOString().split('T')[0];
                    
                    // We update the minimum date for the departure calendar
                    departureInput.setAttribute('min', minDepartureStr);
                    
                    // If the user had already set a departure date earlier than the new rule, we clear it
                    if(departureInput.value && departureInput.value < minDepartureStr) {
                        departureInput.value = '';
                    }
                }
            });
        }
    });



