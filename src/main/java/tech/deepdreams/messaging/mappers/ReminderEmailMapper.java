package tech.deepdreams.messaging.mappers;
import org.mapstruct.Mapper;
import tech.deepdreams.messaging.dtos.ReminderEmailDTO;
import tech.deepdreams.messaging.models.ReminderEmail;

@Mapper(componentModel = "spring")
public interface ReminderEmailMapper {
	
	public ReminderEmailDTO mapModelToDTO (ReminderEmail reminderEmail) ;
}
