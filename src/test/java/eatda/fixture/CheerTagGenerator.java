package eatda.fixture;

import eatda.domain.cheer.Cheer;
import eatda.domain.cheer.CheerTag;
import eatda.domain.cheer.CheerTagName;
import eatda.repository.cheer.CheerTagRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CheerTagGenerator {

    private final CheerTagRepository cheerTagRepository;

    public CheerTagGenerator(CheerTagRepository cheerTagRepository) {
        this.cheerTagRepository = cheerTagRepository;
    }

    public List<CheerTag> generate(Cheer cheer, List<CheerTagName> tagNames) {
        return tagNames.stream()
                .map(name -> cheerTagRepository.save(new CheerTag(cheer, name)))
                .toList();
    }
}
