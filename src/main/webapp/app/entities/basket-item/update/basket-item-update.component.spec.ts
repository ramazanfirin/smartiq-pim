import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BasketItemService } from '../service/basket-item.service';
import { IBasketItem, BasketItem } from '../basket-item.model';
import { IBasket } from 'app/entities/basket/basket.model';
import { BasketService } from 'app/entities/basket/service/basket.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

import { BasketItemUpdateComponent } from './basket-item-update.component';

describe('BasketItem Management Update Component', () => {
  let comp: BasketItemUpdateComponent;
  let fixture: ComponentFixture<BasketItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let basketItemService: BasketItemService;
  let basketService: BasketService;
  let productService: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BasketItemUpdateComponent],
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
      .overrideTemplate(BasketItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BasketItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    basketItemService = TestBed.inject(BasketItemService);
    basketService = TestBed.inject(BasketService);
    productService = TestBed.inject(ProductService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Basket query and add missing value', () => {
      const basketItem: IBasketItem = { id: 456 };
      const basket: IBasket = { id: 69104 };
      basketItem.basket = basket;

      const basketCollection: IBasket[] = [{ id: 35826 }];
      jest.spyOn(basketService, 'query').mockReturnValue(of(new HttpResponse({ body: basketCollection })));
      const additionalBaskets = [basket];
      const expectedCollection: IBasket[] = [...additionalBaskets, ...basketCollection];
      jest.spyOn(basketService, 'addBasketToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ basketItem });
      comp.ngOnInit();

      expect(basketService.query).toHaveBeenCalled();
      expect(basketService.addBasketToCollectionIfMissing).toHaveBeenCalledWith(basketCollection, ...additionalBaskets);
      expect(comp.basketsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Product query and add missing value', () => {
      const basketItem: IBasketItem = { id: 456 };
      const product: IProduct = { id: 61988 };
      basketItem.product = product;

      const productCollection: IProduct[] = [{ id: 62009 }];
      jest.spyOn(productService, 'query').mockReturnValue(of(new HttpResponse({ body: productCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProduct[] = [...additionalProducts, ...productCollection];
      jest.spyOn(productService, 'addProductToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ basketItem });
      comp.ngOnInit();

      expect(productService.query).toHaveBeenCalled();
      expect(productService.addProductToCollectionIfMissing).toHaveBeenCalledWith(productCollection, ...additionalProducts);
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const basketItem: IBasketItem = { id: 456 };
      const basket: IBasket = { id: 36454 };
      basketItem.basket = basket;
      const product: IProduct = { id: 9177 };
      basketItem.product = product;

      activatedRoute.data = of({ basketItem });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(basketItem));
      expect(comp.basketsSharedCollection).toContain(basket);
      expect(comp.productsSharedCollection).toContain(product);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<BasketItem>>();
      const basketItem = { id: 123 };
      jest.spyOn(basketItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basketItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: basketItem }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(basketItemService.update).toHaveBeenCalledWith(basketItem);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<BasketItem>>();
      const basketItem = new BasketItem();
      jest.spyOn(basketItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basketItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: basketItem }));
      saveSubject.complete();

      // THEN
      expect(basketItemService.create).toHaveBeenCalledWith(basketItem);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<BasketItem>>();
      const basketItem = { id: 123 };
      jest.spyOn(basketItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ basketItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(basketItemService.update).toHaveBeenCalledWith(basketItem);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackBasketById', () => {
      it('Should return tracked Basket primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackBasketById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackProductById', () => {
      it('Should return tracked Product primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProductById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
