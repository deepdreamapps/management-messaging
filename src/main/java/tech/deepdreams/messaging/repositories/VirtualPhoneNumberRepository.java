package tech.deepdreams.messaging.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.deepdreams.messaging.models.VirtualPhoneNumber;

public interface VirtualPhoneNumberRepository extends JpaRepository<VirtualPhoneNumber, Long>{
	public List<VirtualPhoneNumber> findByCountryId (Long countryId) ;
}
