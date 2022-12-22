package link.dwsy.ddl.XO.RB;


import link.dwsy.ddl.XO.Enmu.WordType;
import link.dwsy.ddl.entity.WordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordEntityRB {

    private Long id;

    private String word;

    private WordType type;

    public WordEntity toEntity() {
        WordEntity wordEntity = new WordEntity();
        wordEntity.setType(type);
        wordEntity.setId(id);
        wordEntity.setWord(word);
        return wordEntity;
    }
}
