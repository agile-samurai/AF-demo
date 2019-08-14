from rdso import movies


def test_translate_duration():
    assert movies.translate_duration("PT2H22M") == 142
    assert movies.translate_duration("PT2H") == 120
    assert movies.translate_duration("PT23M") == 23
