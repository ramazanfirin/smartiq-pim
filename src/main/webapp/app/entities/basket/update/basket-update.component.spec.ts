import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BasketService } from '../service/basket.service';
import { IBasket, Basket } from '../basket.model';

import { BasketUpdateComponent } from './basket-update.component';

describe('Basket Management Update Component', () => {
  let comp: BasketUpdateComponent;
  let fixture: ComponentFixture<BasketUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let basketService: BasketService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BasketUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BasketUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BasketUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    basketService = TestBed.inject(BasketService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const basket: IBasket = { id: 456 };

      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(basket));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Basket>>();
      const basket = { id: 123 };
      jest.spyOn(basketService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: basket }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(basketService.update).toHaveBeenCalledWith(basket);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Basket>>();
      const basket = new Basket();
      jest.spyOn(basketService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: basket }));
      saveSubject.complete();

      // THEN
      expect(basketService.create).toHaveBeenCalledWith(basket);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Basket>>();
      const basket = { id: 123 };
      jest.spyOn(basketService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(basketService.update).toHaveBeenCalledWith(basket);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
