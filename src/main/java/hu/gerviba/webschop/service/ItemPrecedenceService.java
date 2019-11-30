package hu.gerviba.webschop.service;

import hu.gerviba.webschop.dao.ItemRepository;
import hu.gerviba.webschop.model.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemPrecedenceService {

    @Autowired
    ItemRepository items;

    private static final int FLAG_EDITORS_CHOICE = 1010;
    private static final int FLAG_HIDDEN_EDITORS_CHOICE = 1011;
    private static final int FLAG_FUNKY_ITEM = 1069;

    @Transactional
    public void reorder() {
        List<ItemEntity> all = items.findAll();
        Collections.shuffle(all);

        List<ItemEntity> result = all.stream()
                .filter(item -> item.getFlag() == FLAG_EDITORS_CHOICE || item.getFlag() == FLAG_HIDDEN_EDITORS_CHOICE)
                .collect(Collectors.toCollection(LinkedList::new));

        for (Long circleId : getShuffledCircleIds(all)) {
            result.addAll(all.stream()
                    .filter(item -> item.getCircle().getId().equals(circleId))
                    .sorted((a, b) -> Integer.compare(b.getFlag() % 100, a.getFlag() % 100))
                    .collect(Collectors.toList()));
        }

        Collections.reverse(result);

        for (int i = 0; i < result.size(); i++)
            result.get(i).setPrecedence(i);

        items.saveAll(result);
    }

    private List<Long> getShuffledCircleIds(List<ItemEntity> all) {
        List<Long> circleIds = all.stream()
                .mapToLong(item -> item.getCircle().getId())
                .distinct()
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(circleIds);
        return circleIds;
    }

}
