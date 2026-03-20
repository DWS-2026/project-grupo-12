package es.codeurjc.web.service;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.HotelRepository;
import es.codeurjc.web.repository.UserRepository;


@Service
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;
    
    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            String adminPasswordHash = passwordEncoder.encode("admin");
            User user1 = new User("admin", "admin123@hospedate.com", adminPasswordHash, "ADMIN");
            userRepository.save(user1);
        } else {
            System.out.println("The users already exist in MySQL. Skipping initialization.");
        }

        if (hotelRepository.count() == 0) {
            
            Hotel hotel1 = new Hotel(
                "UMusic Hotel Madrid",
                "Alojamiento Deluxe con vistas al patio",
                "Madrid",
                "Calle de la Paz 11, Centro de Madrid, 28012 Madrid, España",
                742.0,
                "El UMusic Hotel Madrid en Madrid ofrece una experiencia de 5 estrellas con una piscina de agua salada, centro de fitness, terraza solarium, restaurante, bar y WiFi gratis. El check-in y check-out privados, la recepcion 24 horas y el servicio de consejeria garantizan una estancia comoda.",
                Arrays.asList(
                    "/assets/img/portfolio/hoteles/UMusic1.jpg",
                    "/assets/img/portfolio/hoteles/UMusic2.jpg",
                    "/assets/img/portfolio/hoteles/UMusic3.jpg",
                    "/assets/img/portfolio/hoteles/UMusic4.jpg",
                    "/assets/img/portfolio/hoteles/UMusic5.jpg",
                    "/assets/img/portfolio/hoteles/UMusic6.jpg",
                    "/assets/img/portfolio/hoteles/UMusic7.jpg",
                    "/assets/img/portfolio/hoteles/UMusic8.jpg"
                ),
                new HashSet<>(Arrays.asList(
                    "WiFi gratis",
                    "Aire acondicionado",
                    "Baño privado",
                    "Cocina equipada",
                    "Lavadora"
                )),
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
                    "/assets/img/portfolio/hoteles/Edificio_España_2025.jpg",
                    "/assets/img/portfolio/hoteles/Riu2.jpg",
                    "/assets/img/portfolio/hoteles/Riu3.jpg",
                    "/assets/img/portfolio/hoteles/Riu4.jpg",
                    "/assets/img/portfolio/hoteles/Riu5.jpg",
                    "/assets/img/portfolio/hoteles/Riu6.jpg",
                    "/assets/img/portfolio/hoteles/Riu7.jpg",
                    "/assets/img/portfolio/hoteles/Riu8.jpg"
                ),
                new HashSet<>(Arrays.asList(
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
                )),
                4.8
            );

            Hotel hotel3 = new Hotel(
                "Hotel Playacanela en isla Canela",
                "Hotel 4 Estrellas",
                "Huelva",
                "Av. de la Mojarra, 0, 21409, Huelva",
                411.0,
                "El Playacanela es un resort de 4 estrellas muy peculiar visualmente, ya que esta diseñado con un estilo mozárabe. COn sus cúpulas y arcos parece un palacio oriental. Es un hotel masiov y animado, con le objetivo de ser el destino definitivo de las familias. Además cuenta con una localización en primera linea de playa la cual es famosa por su inmensidad. Importante mencionar su cercanía a Portugal estando próximo a la desembocadura del Guadiana y la frontera con el Algarve portugués.",
                Arrays.asList(
                    "/assets/img/portfolio/hoteles/Playacanela1.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela2.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela3.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela4.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela5.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela6.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela7.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela8.jpg",
                    "/assets/img/portfolio/hoteles/Playacanela9.jpg"
                ),
                new HashSet<>(Arrays.asList(
                    "2 Piscinas",
                    "Spa y centro de bienestar",
                    "Traslado aeropuerto",
                    "Parking en el alojamiento",
                    "Habitaciones sin humo",
                    "Bar",
                    "Adaptado personas de movilidad reducida",
                    "Calefacción",
                    "Actividades diarias con monitores cualificados",
                    "Actuaciones nocturnas para las familias"
                )),
                3.8
            );
        
            hotelRepository.saveAll(Arrays.asList(hotel1, hotel2, hotel3));
            System.out.println("Database initialized with hotels.");
            
        } else {
            System.out.println("The hotels already exist in MySQL. Skipping initialization.");
        }
    }
}