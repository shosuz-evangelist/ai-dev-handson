package com.example.ecsite.service;

import com.example.ecsite.dto.CustomerRequest;
import com.example.ecsite.entity.Customer;
import com.example.ecsite.exception.DuplicateEmailException;
import com.example.ecsite.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** 受け入れ基準 2 / 5 / 6 をテストで固定する。 */
class CustomerServiceTest {

    private final CustomerRepository repository = mock(CustomerRepository.class);
    private final CustomerService service = new CustomerService(repository);

    private CustomerRequest req(String name, String email) {
        CustomerRequest r = new CustomerRequest();
        r.setName(name);
        r.setEmail(email);
        r.setPhone("03-0000-0000");
        return r;
    }

    private Customer entity(Long id, String email) {
        Customer c = new Customer();
        c.setId(id);
        c.setName("既存顧客");
        c.setEmail(email);
        return c;
    }

    @Test
    @DisplayName("email が重複したら DuplicateEmailException（受け入れ基準 2）")
    void create_duplicateEmail() {
        when(repository.existsByEmailAndDeletedAtIsNull("a@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req("新規", "a@example.com")))
                .isInstanceOf(DuplicateEmailException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("重複しなければ保存する")
    void create_ok() {
        when(repository.existsByEmailAndDeletedAtIsNull("b@example.com")).thenReturn(false);
        when(repository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer saved = service.create(req("新規", "b@example.com"));

        assertThat(saved.getEmail()).isEqualTo("b@example.com");
        verify(repository).save(any(Customer.class));
    }

    @Test
    @DisplayName("削除は論理削除（deleted_at を立てる）（受け入れ基準 5）")
    void delete_isSoftDelete() {
        Customer c = entity(1L, "c@example.com");
        when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(c));

        assertThat(service.delete(1L)).isTrue();
        assertThat(c.getDeletedAt()).isNotNull();
        verify(repository).save(c);
    }

    @Test
    @DisplayName("存在しない id の削除は false")
    void delete_notFound() {
        when(repository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

        assertThat(service.delete(99L)).isFalse();
    }

    @Test
    @DisplayName("一覧は削除済みを含めない（受け入れ基準 6）")
    void findAll_excludesDeleted() {
        service.findAll();
        verify(repository).findByDeletedAtIsNull();
        verify(repository, never()).findAll();
    }
}
