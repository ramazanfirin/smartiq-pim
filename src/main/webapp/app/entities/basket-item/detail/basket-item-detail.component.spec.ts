import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BasketItemDetailComponent } from './basket-item-detail.component';

describe('BasketItem Management Detail Component', () => {
  let comp: BasketItemDetailComponent;
  let fixture: ComponentFixture<BasketItemDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BasketItemDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ basketItem: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BasketItemDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BasketItemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load basketItem on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.basketItem).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
