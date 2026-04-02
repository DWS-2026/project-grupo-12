package es.codeurjc.web.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.ReserveRepository;

@Service
public class ReserveService {
    @Autowired
    private ReserveRepository reserveRepository;

    public Optional<Reserve> getReserveById(Long id) {
        return reserveRepository.findById(id);
    }

    public void deleteReserve(Long id) {
        reserveRepository.deleteById(id);
    }

    public List<Reserve> getReservesByCustomerId(Long userId) {
        return reserveRepository.findByCustomerId(userId);
    }

    public List<Reserve> getAllReserves() {
        return reserveRepository.findAll();
    }

    public void saveReserve(Reserve reserve) {
        reserveRepository.save(reserve);
    }

    public void saveConfirmedReserve(Reserve reserve) {
        reserve.setStatus("CONFIRMADA");
        reserveRepository.save(reserve);
    }

    public Page<Reserve> getAllReserves(Pageable pageable) {
        return reserveRepository.findAll(pageable);
    }
    
    public Page<Reserve> searchReserves(String keyword, Pageable pageable) {
        return reserveRepository.findByHotelNameContainingIgnoreCaseOrCustomerNameContainingIgnoreCaseOrCustomerEmailContainingIgnoreCase(
            keyword, keyword, keyword, pageable);
    }

    // Creation of pending reservation
    public Reserve createPendingReserve(Hotel hotel, User customer, LocalDate entryDate, LocalDate departureDate, int guests) {
        //We use ChronoUnit to calculate the nights between dates and calculate the price
        long nights = ChronoUnit.DAYS.between(entryDate, departureDate);
        if (nights <= 0) nights = 1; 
        double totalPrice = nights * hotel.getPrice();

        //We created the draft of the reservation using the constructor
        Reserve pendingReserve = new Reserve(hotel, customer, totalPrice, guests, entryDate, departureDate, nights);
        pendingReserve.setStatus("PENDIENTE");
        
        //We saved the reserve in the database;
        reserveRepository.save(pendingReserve);
        return pendingReserve;
    }
}
