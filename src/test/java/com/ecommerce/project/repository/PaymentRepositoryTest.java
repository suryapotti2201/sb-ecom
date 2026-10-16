package com.ecommerce.project.repository;

import com.ecommerce.project.model.Payment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Payment payment;

    @Test
    void saveReturnsPersistedPayment() {
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentRepository.save(payment);

        assertSame(payment, result);
        verify(paymentRepository).save(payment);
    }

    @Test
    void findByIdReturnsPaymentWhenPresent() {
        long paymentId = 1L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentRepository.findById(paymentId);

        assertTrue(result.isPresent());
        assertSame(payment, result.orElseThrow());
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void findByIdReturnsEmptyWhenPaymentIsAbsent() {
        long paymentId = 999L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentRepository.findById(paymentId);

        assertTrue(result.isEmpty());
        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void findAllReturnsAvailablePayments() {
        List<Payment> payments = List.of(payment);
        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentRepository.findAll();

        assertEquals(payments, result);
        verify(paymentRepository).findAll();
    }

    @Test
    void existsByIdReturnsTrueWhenPaymentExists() {
        long paymentId = 1L;
        when(paymentRepository.existsById(paymentId)).thenReturn(true);

        boolean result = paymentRepository.existsById(paymentId);

        assertTrue(result);
        verify(paymentRepository).existsById(paymentId);
    }

    @Test
    void existsByIdReturnsFalseWhenPaymentDoesNotExist() {
        long paymentId = 999L;
        when(paymentRepository.existsById(paymentId)).thenReturn(false);

        boolean result = paymentRepository.existsById(paymentId);

        assertFalse(result);
        verify(paymentRepository).existsById(paymentId);
    }

    @Test
    void countReturnsNumberOfPayments() {
        when(paymentRepository.count()).thenReturn(2L);

        long result = paymentRepository.count();

        assertEquals(2L, result);
        verify(paymentRepository).count();
    }

    @Test
    void deleteByIdDelegatesToRepository() {
        long paymentId = 1L;

        paymentRepository.deleteById(paymentId);

        verify(paymentRepository).deleteById(paymentId);
    }
}
