package com.kishlaly.ta.analyze.tasks;

import com.kishlaly.ta.analyze.tasks.blocks.TaskBlock;
import com.kishlaly.ta.analyze.tasks.blocks.commons.ScreenBasicValidation;
import com.kishlaly.ta.analyze.tasks.blocks.one.Short_ScreenOne_StrictTrendCheck;
import com.kishlaly.ta.analyze.tasks.blocks.two.Long_ScreenTwo_BullishDivergenceMainLogic;

import java.util.ArrayList;
import java.util.List;

import static com.kishlaly.ta.analyze.tasks.Divergencies.BullishConfig.ALLOW_ON_BEARISH_TREND;

/**
 * Для бычьей дивергенции цена акций должна быть выше $3, для медвежьей - выше $10. Объем торгов по
 * акции должен быть выше 500к в сутки (низкий объем торгов означает слабый интерес и частые
 * неожиданные скачки цен)
 * <p>
 * Индикаторы:
 * MACD (12 26 9 close)
 * EMA26 для бОльшего таймфрейма для фильтрации нисходящих трендов
 * <p>
 * Поиск бычьих расхождений происходит в такой последовательности:
 * 1. Гистограмма MACD опускается до самого низкого минимума на отрезке из 100 столбиков
 * (можете задать свое значение). Таким образом определяется дно потенциального бычьего
 * расхождения A-B-C.
 * 2. Гистограмма MACD пересекает нулевую линию снизу вверх, «ломая хребет медведю». Так
 * определяется вершина потенциального бычьего расхождения.
 * 3. Когда акция достигает нового 100-дневного минимума, гистограмма MACD повторно пересекает
 * нулевую линию, но уже сверху вниз. В этой точке сканер и помечает акцию.
 * Пример: https://drive.google.com/file/d/1pd7Y92O3sMRRKHsTbsFoR6uYlhW33CyP/view?usp=sharing
 *
 * @author Vladimir Kishlaly
 * @since 18.11.2021
 */
public class Divergencies extends AbstractTask {

    public static class BullishConfig {
        public static boolean ALLOW_ON_BEARISH_TREND = true; // часто дивергенции бывают на горизонтальных долгосрочных трендах
        public static boolean ALLOW_MULTIPLE_ISLANDS = true;
        public static int MAX_TAIL_SIZE = 7;
        public static int SECOND_BOTTOM_RATIO = 80;
    }

    public static class Default {

        public static List<TaskBlock> buy() {
            return new ArrayList<TaskBlock>() {{
                add(new ScreenBasicValidation());
                if (!ALLOW_ON_BEARISH_TREND) {
                    add(new Short_ScreenOne_StrictTrendCheck());
                }

                add(new Long_ScreenTwo_BullishDivergenceMainLogic());
            }};
        }

    }

}
