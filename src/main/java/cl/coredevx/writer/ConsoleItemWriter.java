package cl.coredevx.writer;

import cl.coredevx.model.Persona;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class ConsoleItemWriter implements ItemWriter<Persona> {

    @Override
    public void write(Chunk<? extends Persona> chunk) {
        for (Persona persona : chunk) {
            System.out.printf("ID: %d | Nombre: %s | Correo: %s%n",
                    persona.getId(), persona.getNombre(), persona.getCorreo());
        }
    }
}
