package tech.deepdreams.messaging.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.deepdreams.messaging.models.VirtualPhoneNumber;

@Repository
public interface PhoneNumberRepository extends JpaRepository<VirtualPhoneNumber, Long>{

	public List<VirtualPhoneNumber> findByCountryId (Long countryId) ;
}
