package es.codeurjc.web.service;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.repository.HotelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    
    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @PostConstruct
    private void initializeDatabase() {
        if (hotelRepository.count() == 0) {
            Hotel hotel1 = new Hotel(
                "UMusic Hotel Madrid",
                "Alojamiento Deluxe con vistas al patio",
                "Madrid",
                "Calle de la Paz 11, Centro de Madrid, 28012 Madrid, España",
                742.0,
                "El UMusic Hotel Madrid en Madrid ofrece una experiencia de 5 estrellas con una piscina de agua salada, centro de fitness, terraza solarium, restaurante, bar y WiFi gratis. El check-in y check-out privados, la recepcion 24 horas y el servicio de consejeria garantizan una estancia comoda.",
                Arrays.asList(
                    "assets/img/portfolio/hoteles/UMusic1.jpg",
                    "assets/img/portfolio/hoteles/UMusic2.jpg",
                    "assets/img/portfolio/hoteles/UMusic3.jpg",
                    "assets/img/portfolio/hoteles/UMusic4.jpg",
                    "assets/img/portfolio/hoteles/UMusic5.jpg",
                    "assets/img/portfolio/hoteles/UMusic6.jpg",
                    "assets/img/portfolio/hoteles/UMusic7.jpg",
                    "assets/img/portfolio/hoteles/UMusic8.jpg"
                ),
                Arrays.asList(
                    "WiFi gratis",
                    "Aire acondicionado",
                    "Baño privado",
                    "Cocina equipada",
                    "Lavadora"
                ),
                5.0
            );
            
            Hotel hotel2 = new Hotel(
                "Hotel Riu Plaza España",
                "Hotel 5 Estrellas",
                "Madrid",
                "Gran Vía, 84, Madrid",
                426.0,
                "Establecimiento muy bien situado en el centro de Madrid y ofrece aire acondicionado en sus habitaciones, un restaurante, WiFi gratuita y un bar. Este hotel cuenta con servicio de habitaciones, servicio de consejería, psicina climatizada al aire libre, centro de fitness, discoteca y recepción 24 horas.",
                Arrays.asList(
                    "assets/img/portfolio/hoteles/Edificio_España_2025.jpg",
                    "assets/img/portfolio/hoteles/Riu2.jpg",
                    "assets/img/portfolio/hoteles/Riu3.jpg",
                    "assets/img/portfolio/hoteles/Riu4.jpg",
                    "assets/img/portfolio/hoteles/Riu5.jpg",
                    "assets/img/portfolio/hoteles/Riu6.jpg",
                    "assets/img/portfolio/hoteles/Riu7.jpg",
                    "assets/img/portfolio/hoteles/Riu8.jpg"
                ),
                Arrays.asList(
                    "Piscina",
                    "Rooftop Bar",
                    "Gimnasio",
                    "Desayuno incluido",
                    "Servicio de habitaciones",
                    "Parking privado",
                    "Discoteca",
                    "Equipamiento audiovisual y tecnológico",
                    "Adaptado silla de ruedas",
                    "Nevera incluida"
                ),
                4.8
            );
        
        hotelRepository.saveAll(Arrays.asList(hotel1, hotel2));
        }
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public void addHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public List<Hotel> getTop3Hotels() {
        return hotelRepository.findTop3ByOrderByRatingDesc();
    }

    public void saveHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
}
